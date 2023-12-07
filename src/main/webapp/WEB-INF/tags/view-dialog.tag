<%@tag description="View Dialog Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="id" required="true" type="java.lang.String"%>
<%@attribute name="title" required="true" type="java.lang.String"%>
<%@attribute name="model" required="true" type="org.jlab.jaws.persistence.model.EntityModel"%>
<div class="dialog" id="${id}-view-dialog" title="${title}">
    <dl>
        <c:forEach items="${model.valueFields}" var="field">
            <dt><c:out value="${field.name}"/></dt>
            <dd class="${field.name}-view"></dd>
        </c:forEach>
    </dl>
</div>