package com.example;

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
    private static final String TABLE_NAME = "parts";

    @Resource(name="jdbc/db")
    DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try(PrintWriter writer = resp.getWriter()) {
            writer.println("<html>\n<head>\n<title>parts</title>\n</head>\n<body>");
            try (Connection connection = dataSource.getConnection()) {
                TableMetaData tableMetaData = TableMetaDataFactory.ofColumnsResultSet(
                        connection.getMetaData().getColumns(null, null, TABLE_NAME, null)
                );
                Map<String, String[]> reqParameterMap = req.getParameterMap();
                try(PreparedStatement preparedStatement =
                            connection.prepareStatement(
                                    String.format("select * from %s %s", TABLE_NAME, buildWhereBlock(tableMetaData, reqParameterMap))
                            )
                ) {
                    setupParameters(preparedStatement, tableMetaData, reqParameterMap);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        writeFilter(resultSet.getMetaData(), writer);
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
            String parameterValue = reqParameterMap.get(field.getName())[0];
            switch (field.getType()) {
                case Types.INTEGER:
                    preparedStatement.setInt(parameterIndex, Integer.parseInt(parameterValue));
                    break;
                case Types.VARCHAR:
                    preparedStatement.setString(parameterIndex, "%" + parameterValue + "%");
                    break;
                case Types.DATE:
                    String parameterValue1 = reqParameterMap.get(field.getName() + AFTER_DATE_FIELD_SUFFIX)[0];
                    String parameterValue2 = reqParameterMap.get(field.getName() + BEFORE_DATE_FIELD_SUFFIX)[0];
                    preparedStatement.setDate(parameterIndex, Date.valueOf(parameterValue1));
                    parameterIndex++;
                    preparedStatement.setDate(parameterIndex, Date.valueOf(parameterValue2));
                    break;
            }
            parameterIndex++;
        }
    }

    private String buildWhereBlock(TableMetaData tableMetaData, Map<String, String[]> parameterMap) {
        if (parameterMap.isEmpty())
            return "";
        List<String> conditions = new ArrayList<>();
        for (FieldMetaData field : tableMetaData.getFields()) {
            if (field.getType() == Types.DATE) {
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
        switch (field.getType()) {
            case Types.INTEGER:
                return String.format("%s >= ?", field.getName());
            case Types.VARCHAR:
                return String.format("%s like ?", field.getName());
            case Types.DATE:
                return String.format("%s between ? and ?", field.getName());
        default:
            throw new AssertionError("");
        }
    }

    private void writeFilter(ResultSetMetaData metaData, PrintWriter writer) throws SQLException {
        writer.format("<form action=\"%s\" method=\"GET\">\n", this.getClass().getSimpleName());
        writer.write("<table>\n");
        writer.write("<th>Filter</th>\n");
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            writer.write("<tr>\n");

            writer.write("<td>");
            String filterId = metaData.getColumnName(i);
            writer.format("<label for=\"%s\">%s</label>", filterId, metaData.getColumnLabel(i));
            writer.write("</td>");

            writer.write("<td>");
            int columnType = metaData.getColumnType(i);
            switch (columnType) {
                case Types.INTEGER:
                    writer.format("<input type=\"number\" name=\"%s\"/>", filterId);
                    break;
                case Types.VARCHAR:
                    writer.format("<input type=\"text\" name=\"%s\"/>", filterId);
                    break;
                case Types.DATE:
                    writer.format("<label for=\"%s\">after</label>\n<input type=\"date\" name=\"%s\"/>", filterId + AFTER_DATE_FIELD_SUFFIX, filterId + AFTER_DATE_FIELD_SUFFIX);
                    writer.format("<label for=\"%s\">before</label>\n<input type=\"date\" name=\"%s\"/>", filterId + BEFORE_DATE_FIELD_SUFFIX, filterId + BEFORE_DATE_FIELD_SUFFIX);
                    break;
                default:
                    throw new AssertionError(String.format("Фильтрация по типу \"%d\" не поддержана", columnType));
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
