<%@tag description="The Inventory Page Template" pageEncoding="UTF-8"%>
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
        <ul>
            <li${'/inventory/instances' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/instances">Instances</a></li>
            <li${'/inventory/classes' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/classes">Classes</a></li>
            <li${'/inventory/locations' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/locations">Locations</a></li>
            <li${'/inventory/categories' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/inventory/categories">Categories</a></li>
        </ul>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>         
</t:page>
