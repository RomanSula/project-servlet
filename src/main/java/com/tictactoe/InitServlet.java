package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession gameSession = request.getSession();

        Field field = new Field();
        List<Sign> data = field.getFieldData();

        gameSession.setAttribute("field", field);
        gameSession.setAttribute("data", data);

        getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
