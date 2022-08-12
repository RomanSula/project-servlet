package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession gameSession = request.getSession();
        Field gameField = extractField(gameSession);
        int index = getSelectedIndex(request);

        Sign currentSign = gameField.getField().get(index);

        // Проверяем, что ячейка, по которой был клик пустая.
        // Иначе ничего не делаем и отправляем пользователя на ту же страницу без изменений
        // параметров в сессии
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // ставим крестик в ячейке, по которой кликнул пользователь
        gameField.getField().put(index, Sign.CROSS);
        if (checkWin(response, gameSession, gameField)){
            return;
        }

        // Получаем пустую ячейку поля
        int emptyFieldIndex = gameField.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            gameField.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if (checkWin(response, gameSession, gameField)){
                return;
            }
        }
        // Если пустой ячейки нет и никто не победил - значит это ничья
        else {
            // Добавляем в сессию флаг, который сигнализирует что произошла ничья
            gameSession.setAttribute("draw", true);

            // Считаем список значков
            List<Sign> data = gameField.getFieldData();

            // Обновляем этот список в сессии
            gameSession.setAttribute("data", data);

            // Шлем редирект
            response.sendRedirect("/index.jsp");
            return;
        }

        List<Sign> data = gameField.getFieldData();

        gameSession.setAttribute("field", gameField);
        gameSession.setAttribute("data", data);

        response.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            // Добавляем флаг, который показывает что кто-то победил
            currentSession.setAttribute("winner", winner);

            // Считаем список значков
            List<Sign> data = field.getFieldData();

            // Обновляем этот список в сессии
            currentSession.setAttribute("data", data);

            // Шлем редирект
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
