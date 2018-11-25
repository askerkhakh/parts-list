package com.example;

import com.example.meta.FieldMetaData;
import com.example.meta.FieldType;
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
import java.sql.Date;
import java.util.*;

@WebServlet("/")
public class PartsServlet extends HttpServlet {

    private static final String AFTER_DATE_FIELD_SUFFIX = "_after";
    private static final String BEFORE_DATE_FIELD_SUFFIX = "_before";
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
                Map<String, String[]> reqParameterMap = req.getParameterMap();
                try(PreparedStatement preparedStatement =
                            connection.prepareStatement(
                                    String.format("select * from %s %s", tableMetaData.getName(), buildWhereBlock(tableMetaData, reqParameterMap))
                            )
                ) {
                    setupParameters(preparedStatement, tableMetaData, reqParameterMap);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        writeFilter(tableMetaData, writer);
                        writeTable(resultSet, writer);
                    }
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            writer.write("</body>\n</html>");
        }
    }

    private void setupParameters(PreparedStatement preparedStatement, TableMetaData tableMetaData, Map<String, String[]> reqParameterMap) throws SQLException {
        if (reqParameterMap.isEmpty())
            return;
        int parameterIndex = 1;
        for (FieldMetaData field : tableMetaData.getFields()) {
            switch (field.getType()) {
                case DATE:
                    String afterValue = reqParameterMap.get(field.getName() + AFTER_DATE_FIELD_SUFFIX)[0];
                    String beforeValue = reqParameterMap.get(field.getName() + BEFORE_DATE_FIELD_SUFFIX)[0];
                    if (afterValue.isEmpty() || beforeValue.isEmpty())
                        continue;
                    preparedStatement.setDate(parameterIndex, Date.valueOf(afterValue));
                    parameterIndex++;
                    preparedStatement.setDate(parameterIndex, Date.valueOf(beforeValue));
                    break;
                default:
                    String parameterValue = reqParameterMap.get(field.getName())[0];
                    if (parameterValue.isEmpty())
                        continue;
                    switch (field.getType()) {
                        case INTEGER:
                            preparedStatement.setInt(parameterIndex, Integer.parseInt(parameterValue));
                            break;
                        case STRING:
                            preparedStatement.setString(parameterIndex, "%" + parameterValue + "%");
                            break;
                    }
            }
            parameterIndex++;
        }
    }

    private String buildWhereBlock(TableMetaData tableMetaData, Map<String, String[]> parameterMap) {
        if (parameterMap.isEmpty())
            return "";
        List<String> conditions = new ArrayList<>();
        for (FieldMetaData field : tableMetaData.getFields()) {
            if (field.getType() == FieldType.DATE) {
                String afterValue = parameterMap.get(field.getName() + AFTER_DATE_FIELD_SUFFIX)[0];
                String beforeValue = parameterMap.get(field.getName() + BEFORE_DATE_FIELD_SUFFIX)[0];
                if (afterValue.isEmpty() || beforeValue.isEmpty())
                    continue;
            }
            else
                if (parameterMap.get(field.getName())[0].isEmpty())
                    continue;
            conditions.add(buildCondition(field));
        }
        return "where " + String.join(" and ", conditions);
    }

    private String buildCondition(FieldMetaData field) {
        // TODO: оборачивать в кавычки только при необходимости
        String quotedName = "\"" + field.getName() + "\"";
        switch (field.getType()) {
            case INTEGER:
                return String.format("%s >= ?", quotedName);
            case STRING:
                return String.format("%s like ?", quotedName);
            case DATE:
                return String.format("%s between ? and ?", quotedName);
        default:
            throw new AssertionError("");
        }
    }

    private void writeFilter(TableMetaData metaData, PrintWriter writer) {
        writer.format("<form action=\"%s\" method=\"GET\">\n", this.getClass().getSimpleName());
        writer.write("<table>\n");
        writer.write("<th>Filter</th>\n");
        for (FieldMetaData field : metaData.getFields()) {
            writer.write("<tr>\n");

            writer.write("<td>");
            String filterId = field.getName();
            writer.format("<label for=\"%s\">%s</label>", filterId, field.getName());
            writer.write("</td>");

            writer.write("<td>");
            FieldType columnType = field.getType();
            switch (columnType) {
                case INTEGER:
                    writer.format("<input type=\"number\" name=\"%s\"/>", filterId);
                    break;
                case STRING:
                    writer.format("<input type=\"text\" name=\"%s\"/>", filterId);
                    break;
                case DATE:
                    writer.format("<label for=\"%s\">after</label>\n<input type=\"date\" name=\"%s\"/>", filterId + AFTER_DATE_FIELD_SUFFIX, filterId + AFTER_DATE_FIELD_SUFFIX);
                    writer.format("<label for=\"%s\">before</label>\n<input type=\"date\" name=\"%s\"/>", filterId + BEFORE_DATE_FIELD_SUFFIX, filterId + BEFORE_DATE_FIELD_SUFFIX);
                    break;
                default:
                    throw new AssertionError(String.format("Фильтрация по типу \"%s\" не поддержана", columnType.toString()));
            }
            writer.write("</td>");

            writer.write("</tr>\n");
        }
        writer.write("</table>\n");
        writer.write("<input type=\"submit\" value=\"Filter\"/>");
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
