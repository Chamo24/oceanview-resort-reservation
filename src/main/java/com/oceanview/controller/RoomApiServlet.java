package com.oceanview.controller;

import com.google.gson.Gson;
import com.oceanview.model.Room;
import com.oceanview.service.RoomService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RoomApiServlet - REST API Web Service for Room data
 * URL: /api/rooms
 * Returns JSON data for AJAX requests from frontend
 * This makes the application a Distributed Application with Web Services
 */
@WebServlet("/api/rooms")
public class RoomApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private RoomService roomService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        roomService = new RoomService();
        gson = new Gson();
    }

    /**
     * GET - Returns room data as JSON
     * /api/rooms - Get all rooms
     * /api/rooms?type=Single - Get available rooms by type
     * /api/rooms?action=count - Get available room counts
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String roomType = request.getParameter("type");
        String action = request.getParameter("action");

        try {
            if ("count".equals(action)) {
                // Return available room counts for all types
                Map<String, Integer> counts = new HashMap<>();
                counts.put("Single", roomService.getAvailableRoomCount("Single"));
                counts.put("Double", roomService.getAvailableRoomCount("Double"));
                counts.put("Deluxe", roomService.getAvailableRoomCount("Deluxe"));
                counts.put("Suite", roomService.getAvailableRoomCount("Suite"));

                out.print(gson.toJson(counts));

            } else if (roomType != null && !roomType.isEmpty()) {
                // Return available rooms for specific type
                List<Room> rooms = roomService.getAvailableRoomsByType(roomType);
                out.print(gson.toJson(rooms));

            } else {
                // Return all rooms
                List<Room> rooms = roomService.getAllRooms();
                out.print(gson.toJson(rooms));
            }

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            out.print(gson.toJson(error));
        }

        out.flush();
    }
}