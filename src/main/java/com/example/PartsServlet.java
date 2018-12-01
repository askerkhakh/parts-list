package com.example;

import com.example.filter.Filter;
import com.example.filter.FilterFactory;
import com.example.filter.FilterItem;
import com.example.meta.FieldMetaData;
import com.example.meta.TableMetaData;
import com.example.meta.TableMetaDataFactory;
import com.example.sorting.SortInfo;
import com.example.sorting.SortInfoFactory;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/")
public class PartsServlet extends HttpServlet {

    private TableMetaData tableMetaData;

    @Resource(name="jdbc/db")
    DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        tableMetaData = TableMetaDataFactory.partsTable();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        try(PrintWriter writer = resp.getWriter()) {
            writer.println("<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"utf-8\"/><title>parts</title>\n</head>\n<body>");
            try (Connection connection = dataSource.getConnection()) {
                Map<String, String[]> reqParameterMap = req.getParameterMap();
                Filter filter = FilterFactory.newInstance(tableMetaData, reqParameterMap);
                SortInfo sortInfo = SortInfoFactory.newInstance(reqParameterMap);
                List<Object> parameterList = new ArrayList<>();
                try(PreparedStatement preparedStatement =
                            connection.prepareStatement(
                                    String.format("select * from %s %s %s",
                                            tableMetaData.getName(),
                                            buildWhereBlock(filter, parameterList),
                                            buildOrderBy(sortInfo))
                            )
                ) {
                    setupParameters(preparedStatement, parameterList);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        writeFilter(filter, writer);
                        writeTable(tableMetaData, filter, sortInfo, resultSet, writer);
                    }
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            writer.write("</body>\n</html>");
        }
    }

    private String buildOrderBy(SortInfo sortInfo) {
        String orderByString = sortInfo.buildOrderBy();
        if (orderByString.isEmpty())
            return "";
        return "order by " + orderByString;
    }

    private void setupParameters(PreparedStatement preparedStatement, List<Object> parameterList) throws SQLException {
        int parameterIndex = 1;
        for (Object parameter : parameterList) {
            preparedStatement.setObject(parameterIndex++, parameter);
        }
    }

    private String buildWhereBlock(Filter filter, List<Object> parameterList) {
        List<String> conditions = new ArrayList<>();
        for (FilterItem filterItem : filter) {
            if (!filterItem.isEmpty())
                conditions.add(filterItem.buildCondition(parameterList));
        }
        if (conditions.isEmpty())
            return "";
        else
            return "where " + String.join(" and ", conditions);
    }

    private void writeFilter(Filter filter, PrintWriter writer) {
        writer.format("<form action=\"%s\" method=\"GET\">\n", this.getClass().getSimpleName());
        writer.write("<table>\n");
        writer.write("<tr><th>filter</th></tr>\n");
        for (FilterItem filterItem : filter) {
            writer.write("<tr>\n");

            writer.write("<td>");
            writer.write(filterItem.getLabelTag());
            writer.write("</td>");

            writer.write("<td>");
            writer.write(filterItem.getInputTag());
            writer.write("</td>");

            writer.write("</tr>\n");
        }
        writer.write("</table>\n");
        writer.write("<input type=\"submit\" value=\"filter\"/>");
        writer.write("</form>\n");
    }

    private void writeTable(TableMetaData tableMetaData, Filter filter, SortInfo sortInfo, ResultSet resultSet, PrintWriter writer) throws SQLException {
        writer.write("<table>");
        writeTableHeader(tableMetaData, filter, sortInfo, writer);
        writeTableData(tableMetaData, resultSet, writer);
        writer.write("</table>");
    }

    private void writeTableData(TableMetaData tableMetaData, ResultSet resultSet, PrintWriter writer) throws SQLException {
        while (resultSet.next()) {
            writer.write("<tr>\n");
            int i = 1;
            for (FieldMetaData fieldMetaData : tableMetaData.getFields()) {
                writer.write("<td>" + getDisplayValue(fieldMetaData, resultSet, i++) + "</td>");
            }
            writer.write("</tr>\n");
        }
    }

    private String getDisplayValue(FieldMetaData fieldMetaData, ResultSet resultSet, int columnIndex) throws SQLException {
        if (resultSet.getObject(columnIndex) == null)
            return "";
        switch (fieldMetaData.getType()) {
            case DATE:
                return resultSet.getDate(columnIndex).toString();
            case STRING:
                return resultSet.getString(columnIndex);
            case INTEGER:
                return Integer.valueOf(resultSet.getInt(columnIndex)).toString();
            default:
                throw new AssertionError();
        }
    }


    private void writeTableHeader(TableMetaData tableMetaData, Filter filter, SortInfo sortInfo, PrintWriter writer) {
        writer.write("<tr>");
        for (FieldMetaData fieldMetaData : tableMetaData.getFields()) {
            writer.write("<th>");
            writer.write(String.format("<a href=\"%s\">%s</a>", buildRef(fieldMetaData, filter, sortInfo) , fieldMetaData.getLabel()));
            writer.write("</th>");
        }
        writer.write("</tr>");
    }

    private String buildRef(FieldMetaData fieldMetaData, Filter filter, SortInfo sortInfo) {
        String ref = "?" + String.join("&", filter.toQueryString(), sortInfo.getQueryStringForField(fieldMetaData));
        ref = ref.replaceAll("&", "&amp;");
        return ref;
    }

}
