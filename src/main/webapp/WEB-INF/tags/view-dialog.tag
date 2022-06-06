<%@tag description="View Dialog Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="id" required="true" type="java.lang.String"%>
<%@attribute name="title" required="true" type="java.lang.String"%>
<%@attribute name="fields" required="true" type="java.util.List"%>
<div class="dialog" id="${id}-view-dialog" title="${title}">
    <dl>
        <c:forEach items="${fields}" var="field">
            <c:if test="${!field.isKey()}">
                <dt><c:out value="${field.name}"/></dt>
                <dd class="${field.name}-view"></dd>
            </c:if>
        </c:forEach>
    </dl>
</div>