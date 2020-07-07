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
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.Report;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {

  private Map<String, String> PARAM_DEFAULT_MAP = new HashMap<String, String>() {
    {
      put("title", "");
      put("latitude", "0.0");
      put("longitude", "0.0");
      put("timestamp", "0");
      put("incidentType", "etc");
      put("description", "");
    }
  };

  private DateFormat timeStampFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");

  public String test = "Test";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println("<h1>Hello world!</h1>");
  }

  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity reportEntity = new Entity("Report");

    for (String paramName : PARAM_DEFAULT_MAP.keySet()) {
      String defaultValue = PARAM_DEFAULT_MAP.get(paramName);
      String value = getParameter(request, paramName, defaultValue);
      switch (paramName) {
        case "latitude":
        case "longitude":
          reportEntity.setProperty(paramName, Double.parseDouble(value));
          break;
        case "timestamp":
          try {
            Date timestamp = timeStampFormatter.parse(value);
            reportEntity.setProperty(paramName, timestamp.getTime());
          } catch (Exception exception) {
            response.getWriter().println(exception);
          }
          break;
        default:
          reportEntity.setProperty(paramName, value);
      }
    }

    reportEntity.setProperty("imageUrl", getUploadedFileUrl(request, "image"));
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(reportEntity);
  }

  /**
   * @return the request parameter, or the default value if the parameter was not
   *         specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Returns a URL that points to the uploaded file, or null if the user didn't
   * upload a file.
   */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    // User submitted form without selecting a file, so we can't get a URL. (dev
    // server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    } else {
      return "/serve?blob-key=" + blobKeys.get(0).getKeyString();
    }
  }
}
