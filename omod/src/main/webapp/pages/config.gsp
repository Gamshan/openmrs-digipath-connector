<% ui.decorateWith("appui", "standardEmrPage") %>

<style>
        .form-container {
            max-width: 500px;
            margin: 20px auto;
            border: 2px solid #333; /* The main border */
            padding: 20px;
            border-radius: 8px;
            font-family: sans-serif;
        }

        .form-row {
            display: flex;
            flex-direction: column;
            margin-bottom: 15px; /* Spacing between "rows" */
        }

        label {
            font-weight: bold;
            margin-bottom: 5px;
        }

        input {
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        button {
            padding: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }


         header,
            nav,
            .navbar,
            ul.nav,
            .breadcrumbs {
                visibility: hidden !important;
                height: 0 !important;
                overflow: hidden !important;
            }

    </style>


<h3>Digipath Connector Configuration</h3>

<div class="form-container">
<h4>${binding.hasVariable('editConnector') && editConnector ? 'Update data' : 'Add new data'}</h4>
<hr/>
    <form method="post">

        <div class="form-row">
            <label>URL:</label>
            <input type="text"
             value="${editConnector?.url ? editConnector.url : ''}"
             name="url" required />
        </div>

        <div class="form-row">
                <label>Username:</label>
                <input type="text" value="${editConnector?.username ? editConnector.username : ''}" name="username" required />
           </div>

        <div class="form-row">
                <label>Description:</label>
                <input type="text"
                name="description"
                value="${editConnector?.description ? editConnector.description :  ''}"
                required />
        </div>


          <div>
          <% if (editConnector) { %>
                  <button type="button"
                          class="button"
                          onclick="window.location='${ui.pageLink("digipath.connector", "config")}'">
                      Back
                  </button>
              <% } %>

            <button type="submit">${binding.hasVariable('editConnector') && editConnector ? 'Update' : 'Save'}</button>
        </div>

    </form>
</div>
</hr>

<table class="table">
    <tr>
        <th>ID</th>
        <th>Url</th>
        <th>Username</th>
        <th>Description</th>
        <th></th>
    </tr>

    <% connectors.each { connector -> %>
       <tr>
            <td>${connector.id}</td>
            <td>${connector.url}</td>
            <td>${connector.username}</td>
            <td>${connector.description}</td>

            <td>

             <button type="button"
                                 class="button"
                                 onclick="toggleRow(${connector.id}); event.stopPropagation();">
                             View
             </button>

             <button type="button"
                     class="button"
                     onclick="window.location='?editId=${connector.id}'">
                 Edit
             </button>



              <form method="post" style="display:inline;">
                  <input type="hidden" name="deleteId" value="${connector.id}" />
                  <button type="submit"
                  onclick="return confirm('Are you sure you want to delete this connector?');">
                          Delete
                      </button>
              </form>

            </td>
        </tr>

        <tr id="details-${connector.id}" style="display:none; background:#f9f9f9;">
            <td colspan="4">

                <%
                def protocolObj = connector?.protocol ?
                        new groovy.json.JsonSlurper().parseText(connector.protocol)
                        : null
                %>

                <% protocolObj?.data?.tasks?.each { task -> %>
                    <p>• ${task?.caption}</p>
                <% } %>

            </td>
        </tr>

    <% } %>
</table>

<script>
    function toggleRow(id) {
        var row = document.getElementById("details-" + id);
        if (row.style.display === "none") {
            row.style.display = "table-row";
        } else {
            row.style.display = "none";
        }
    }
</script>
