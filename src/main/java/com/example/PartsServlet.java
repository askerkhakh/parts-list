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

@WebServlet("/")
public class PartsServlet extends HttpServlet {

    private static final String FIRST_DATE_FIELD_ID_SUFFIX = "_after";
    @Resource(name="jdbc/db")
    DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try(PrintWriter writer = resp.getWriter()) {
            writer.println("<html>\n<head>\n<title>parts</title>\n</head>\n<body>");
            try (
                    Connection connection = dataSource.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("select * from parts");
                    ResultSet resultSet = preparedStatement.executeQuery()
            ) {
                writeFilter(resultSet.getMetaData(), writer);
                writeTable(resultSet, writer);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            writer.write("</body>\n</html>");
        }
    }

    private void writeFilter(ResultSetMetaData metaData, PrintWriter writer) throws SQLException {
        writer.format("<form action=\"%s\" method=\"GET\">\n", this.getClass().getSimpleName());
        writer.write("<table>\n");
        writer.write("<th>Filter</th>\n");
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            writer.write("<tr>\n");

            writer.write("<td>");
            String columnName = metaData.getColumnName(i);
            String filterId = columnName;
            if (metaData.getColumnType(i) == Types.DATE)
                filterId += FIRST_DATE_FIELD_ID_SUFFIX;
            writer.format("<label for=\"%s\">%s</label>", filterId, metaData.getColumnLabel(i));
            writer.write("</td>");

            writer.write("<td>");
            switch (metaData.getColumnType(i)) {
                case Types.INTEGER:
                    writer.format("<input type=\"number\" name=\"%s\"/>", filterId);
                    break;
                case Types.VARCHAR:
                    writer.format("<input type=\"text\" name=\"%s\"/>", filterId);
                    break;
                case Types.DATE:
                    writer.format("<label for=\"%s\">after</label>\n<input type=\"date\" name=\"%s\"/>", filterId, filterId);
                    writer.format("<label for=\"%s\">before</label>\n<input type=\"date\" name=\"%s\"/>", columnName + "_before", columnName + "_before");
                    break;
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
