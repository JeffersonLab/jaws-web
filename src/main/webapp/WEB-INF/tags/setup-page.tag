<%@tag description="The Setup Page Template" pageEncoding="UTF-8"%>
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
        <h2 id="left-column-header">Setup</h2>
        <ul>
            <li${'/setup/transfer' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/setup/transfer">Transfer</a></li>
            <li${'/setup/syncs' eq currentPath ? ' class="current-secondary"' : ''}><a href="${pageContext.request.contextPath}/setup/syncs">Syncs</a></li>
        </ul>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>         
</t:page>
