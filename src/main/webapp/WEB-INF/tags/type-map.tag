<%@tag description="Module Map Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="id" required="true" type="java.lang.String"%>
<%@attribute name="model" required="true" type="org.jlab.jaws.model.EntityModel"%>
jawsTypes.${id} = new Map();
<c:forEach items="${model.keyFields}" var="field">
    jawsTypes.${id}.set("${field.name}", "${field.type.name()}");
</c:forEach>
<c:forEach items="${model.valueFields}" var="field">
    jawsTypes.${id}.set("${field.name}", "${field.type.name()}");
</c:forEach>