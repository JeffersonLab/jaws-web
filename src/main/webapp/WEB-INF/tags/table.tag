<%@tag description="Table Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="id" required="true" type="java.lang.String"%>
<%@attribute name="fields" required="true" type="java.util.List"%>
<c:set var="columnCount" value="0"/>
<c:forEach items="${fields}" var="field">
    <c:if test="${field.inTable}">
        <c:set var="columnCount" value="${columnCount + 1}"/>
    </c:if>
</c:forEach>
<table class="data-table scroll-table stripped-table uniselect-table" id="${id}-table" style="--column-width:${100 / columnCount}%;">
    <thead>
        <tr>
        <c:forEach items="${fields}" var="field">
            <c:if test="${field.inTable}">
                <th><c:out value="${field.name}"/></th>
            </c:if>
        </c:forEach>
        </tr>
    </thead>
    <tbody></tbody>
</table>