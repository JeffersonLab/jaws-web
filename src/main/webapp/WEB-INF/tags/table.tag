<%@tag description="Table Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="id" required="true" type="java.lang.String"%>
<%@attribute name="model" required="true" type="org.jlab.jaws.model.EntityModel"%>
<c:set var="columnCount" value="${model.tableColumns.size()}"/>
<table class="data-table scroll-table stripped-table uniselect-table" id="${id}-table" style="--column-width:${100 / columnCount}%;">
    <thead>
        <tr>
        <c:forEach items="${model.tableColumns}" var="field">
            <th><c:out value="${field}"/></th>
        </c:forEach>
        </tr>
    </thead>
    <tbody></tbody>
</table>