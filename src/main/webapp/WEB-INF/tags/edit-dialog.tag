<%@tag description="Edit Dialog Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="id" required="true" type="java.lang.String"%>
<%@attribute name="title" required="true" type="java.lang.String"%>
<%@attribute name="fields" required="true" type="java.util.List"%>
<div class="dialog" id="${id}-edit-dialog2" title="${title}">
    <form onsubmit="return false;">
        <fieldset>
            <c:forEach items="${fields}" var="field">
                <label><c:out value="${field.name}"/></label>
                <input type="text" id="${field.name}-input"/>
            </c:forEach>
        </fieldset>
        <input class="edit-dialog-submit" type="submit" tabindex="-1" style="position:absolute; top:-1000px"/>
    </form>
</div>