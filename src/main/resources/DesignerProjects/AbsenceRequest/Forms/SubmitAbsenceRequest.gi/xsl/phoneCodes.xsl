<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<data jsxid="jsxroot"> 
<xsl:apply-templates/>
</data>
</xsl:template>
<xsl:template match="country">
<record jsxtext="{concat('+',@phoneCode)}" jsxid="{@phoneCode}"/>
</xsl:template>
</xsl:stylesheet>