<%@tag description="Module Map Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="id" required="true" type="java.lang.String"%>
<%@attribute name="fields" required="true" type="java.util.List"%>
jawsTypes.${id} = new Map();
<c:forEach items="${fields}" var="field">
    jawsTypes.${id}.set("${field.name}", "${field.type.name()}");
</c:forEach>