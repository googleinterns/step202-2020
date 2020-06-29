// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.Report;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/report")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println("<h1>Hello world!</h1>");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String title = getParameter(request, "title", "");
    double latitude = getParameter(request, "latitude", 0.0);
    double longitude = getParameter(request, "longitude", 0.0);
    long timestamp = getParameter(request, "timestamp", System.currentTimeMillis());
    String incidentType = getParameter(request, "incidentType", "etc");
    String description = getParameter(request, "description", "");

    Entity reportEntity = new Entity("Report");
    reportEntity.setProperty("title", title);
    reportEntity.setProperty("latitude", latitude);
    reportEntity.setProperty("longitude", longitude);
    reportEntity.setProperty("timestamp", timestamp);
    reportEntity.setProperty("incidentType", incidentType);
    reportEntity.setProperty("description", description);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(reportEntity);
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
