<%@tag description="The Inventory Page Template" pageEncoding="UTF-8"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@attribute name="title"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<t:page title="${title}">
    <jsp:attribute name="stylesheets">       
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="secondaryNavigation">
        <h2 id="left-column-header">Inventory</h2>
        <ul>
            <li${'/inventory/instances' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/instances">Instances</a></li>
            <li${fn:startsWith(currentPath, '/inventory/classes') ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/classes">Classes</a></li>
            <li${'/inventory/locations' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/locations">Locations</a></li>
            <li${'/inventory/priorities' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/priorities">Priorities</a></li>
            <li${'/inventory/teams' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/teams">Teams</a></li>
            <li${'/inventory/categories' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/categories">Categories</a></li>
        </ul>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>         
</t:page>
