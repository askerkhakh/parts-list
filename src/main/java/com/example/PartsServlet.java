package com.example;

import com.example.filter.Filter;
import com.example.filter.FilterFactory;
import com.example.filter.FilterItem;
import com.example.meta.TableMetaData;
import com.example.meta.TableMetaDataFactory;

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
        try(PrintWriter writer = resp.getWriter()) {
            writer.println("<html>\n<head>\n<title>parts</title>\n</head>\n<body>");
            try (Connection connection = dataSource.getConnection()) {
                Filter filter = FilterFactory.newInstance(tableMetaData, req.getParameterMap());
                List<Object> parameterList = new ArrayList<>();
                try(PreparedStatement preparedStatement =
                            connection.prepareStatement(
                                    String.format("select * from %s %s", tableMetaData.getName(), buildWhereBlock(filter, parameterList))
                            )
                ) {
                    setupParameters(preparedStatement, parameterList);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        writeFilter(filter, writer);
                        writeTable(resultSet, writer);
                    }
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            writer.write("</body>\n</html>");
        }
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
        writer.write("<th>filter</th>\n");
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
        writer.write("<form>\n");
    }

    private void writeTable(ResultSet resultSet, PrintWriter writer) throws SQLException {
        writer.write("<table>");
        ResultSetMetaData metaData = resultSet.getMetaData();
        writeTableHeader(metaData, writer);
        writeTableData(metaData, resultSet, writer);
        writer.write("</table>");
    }

    private void writeTableData(ResultSetMetaData metaData, ResultSet resultSet, PrintWriter writer) throws SQLException {
        while (resultSet.next()) {
            writer.write("<tr>\n");
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                writer.write("<td>" + resultSet.getString(i) + "</td>");
            }
            writer.write("</tr>\n");
        }
    }

    private void writeTableHeader(ResultSetMetaData metaData, PrintWriter writer) throws SQLException {
        writer.write("<tr>");
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            writer.write("<th>" + metaData.getColumnLabel(i) + "</th>");
        }
        writer.write("</tr>");
    }

}
