<%@tag description="Table Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="id" required="true" type="java.lang.String"%>
<%@attribute name="fields" required="true" type="java.util.List"%>
<table class="data-table outer-table uniselect-table" id="${id}-table">
    <thead>
        <tr>
            <c:set value="${0}" var="fieldCount"/>
            <c:forEach items="${fields}" var="field">
                <c:if test="${field.inTable}">
                    <c:set value="${fieldCount + 1}" var="fieldCount"/>
                    <th><c:out value="${field.name}"/></th>
                </c:if>
            </c:forEach>
            <th class="scrollbar-header"><span class="expand-icon" title="Expand Table"></span></th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td class="inner-table-cell" colspan="${fieldCount + 1}">
                <div class="pane-decorator">
                    <div class="table-scroll-pane">
                        <table class="inner-table data-table stripped-table">
                            <tbody></tbody>
                        </table>
                    </div>
                </div>
            </td>
        </tr>
    </tbody>
</table>