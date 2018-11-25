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
                writeTable(resultSet, writer);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            writer.write("</body>\n</html>");
        }
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
