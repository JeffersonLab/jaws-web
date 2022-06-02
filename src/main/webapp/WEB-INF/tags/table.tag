<%@tag description="Table Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="id" required="true" type="java.lang.String"%>
<%@attribute name="fields" required="true" type="java.util.List"%>
<table class="data-table stripped-table uniselect-table" id="${id}-table">
    <thead>
        <tr>
        <c:forEach items="${fields}" var="field">
            <th><c:out value="${field.name}"/></th>
        </c:forEach>
        </tr>
    </thead>
    <tbody></tbody>
</table>