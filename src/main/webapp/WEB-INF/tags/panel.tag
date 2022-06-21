<%@tag description="Panel Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@attribute name="id" required="true" type="java.lang.String"%>
<%@attribute name="title" required="true" type="java.lang.String"%>
<%@attribute name="model" required="true" type="org.jlab.jaws.model.EntityModel"%>
<%@attribute name="editable" required="false" type="java.lang.Boolean"%>
<div class="panel" id="${id}-panel">
    <div class="toolbar">
        <form class="search-form" onsubmit="return false;">
            <input type="text" value="" class="search-input" placeholder="field=value,field~value"/>
            <button type="button" class="search-button">Search</button> |
        </form>
        <c:if test="${editable}">
            <button type="button" class="new-button">New</button> |
        </c:if>
        <button type="button" class="selected-row-action view-button" disabled="disabled">View</button>
        <c:if test="${editable}">
            <button type="button" class="selected-row-action edit-button" disabled="disabled">Edit</button>
            <button type="button" class="selected-row-action delete-button" disabled="disabled">Delete</button> |
        </c:if>
        <button type="button" class="prev-button" disabled="disabled">Previous</button>
        <button type="button" class="next-button" disabled="disabled">Next</button> |
        <span>Records: </span><span class="record-count"></span>
    </div>
    <div class="table-wrap">
        <t:table id="${id}" model="${model}"/>
    </div>
    <t:view-dialog id="${id}" title="${title}" model="${model}"/>
</div>