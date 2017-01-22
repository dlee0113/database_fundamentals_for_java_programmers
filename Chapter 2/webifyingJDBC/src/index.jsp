<%@ page language = "java" contentType = "text/html; charset = UTF-8" pageEncoding = "UTF-8"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix="c" %>	
<%@ taglib uri = "http://java.sun.com/jsp/jstl/sql" prefix="sql" %>   
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!doctype html>					  
<html>
  <head>
    <title>RecClub Activities</title>
    <link rel = "stylesheet" href = "style.css" type = "text/css"></link>
  </head>
  <body>
    
    <sql:setDataSource                    
      var = "myDS"                                           
      driver = "org.sqlite.JDBC"
      url = "jdbc:sqlite://Users/martinkalin/dbBasicsM/ch2/web/recClub.db"
    />
    
    <sql:query var = "activities" dataSource = "${myDS}"> 
      SELECT * FROM activities ORDER BY name;
    </sql:query>

   <center><h3>RecClub Activities</h3></center>
    <div align="center">
      <form>
	<table class = "products" border = "1" cellpadding = "5">
          <tr>
	    <th>Get session info</th>
	    <th>Activity</th>
	    <th>Price</th>
          </tr>
	  <c:set var = 'ind' value = '1' scope = 'page'/>
	  <fmt:setLocale value = "en_US"/>
          <c:forEach var = "item" items = "${activities.rows}"> 
            <tr>
	      <td><input type = "checkbox" name = "check-${ind}" value ="${ind}"/></td>
              <td><input style = "text-align:center;"
			  name ="prod-${ind}"
			  type = "text"
			  value = "${item.name}" readonly/></td>
	      <td><input type = "text"     
			 style = "text-align:center;"
			 name = "price-${ind}" 
			 value = "<fmt:formatNumber type = 'currency' 
                                                    maxFractionDigits = '2' 
                                                    value = '${item.cost}'/>"
			 readonly/></td>
            </tr>
	    <c:set var = 'ind' value = '${ind + 1}'/>
          </c:forEach>
	</table>
	<p><input type = "submit" value = " Submit requests "/>
      </form>
    </div>
  </body>
</html>
