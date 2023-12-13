<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Alarm Coordinates"/>
<t:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/coordinates.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <section>                              
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"></div>
            <div id="chart-wrap" class="chart-wrap-backdrop">
                <button type="button" disabled="disabled">Flush</button>
                <table class="data-table outer-table">
                    <thead>
                    <tr>
                        <th>Topic</th>
                        <th>Offset</th>
                        <th>Last Processed</th>
                        <th>Materialized Records</th>
                        <th class="scrollbar-header"><span class="expand-icon" title="Expand Table"></span></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td class="inner-table-cell" colspan="5">
                            <div class="pane-decorator">
                                <div class="table-scroll-pane">
                                    <table class="data-table inner-table stripped-table">
                                        <tbody>
                                        <c:forEach items="${coordinateList}" var="coordinate">
                                            <tr data-id="${coordinate.coordinateId}">
                                                <td><c:out value="${coordinate.topic}"/></td>
                                                <td><c:out value="${coordinate.offset}"/></td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </section>
    </jsp:body>         
</t:setup-page>