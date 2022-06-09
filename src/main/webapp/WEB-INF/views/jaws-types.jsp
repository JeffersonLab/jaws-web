<%@page contentType="application/javascript" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
const jawsTypes = {};
<t:type-map id="registrations" fields="${registrationFields}"/>
<t:type-map id="activations" fields="${activationFields}"/>
<t:type-map id="overrides" fields="${overrideFields}"/>
<t:type-map id="classes" fields="${classFields}"/>
<t:type-map id="instances" fields="${instanceFields}"/>
<t:type-map id="locations" fields="${locationFields}"/>
<t:type-map id="categories" fields="${categoryFields}"/>
export default jawsTypes;