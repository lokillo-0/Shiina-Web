<#assign noBeatmapIcon = "https://shiina.osunolimits.dev/img/nobeatmapicon.png"> 
<#function convertStatusBack status>
    <#switch status>
        <#case 0><#return "Not submitted"><#break>
        <#case 1><#return "Pending"><#break>
        <#case 2><#return "Ranked"><#break>
        <#case 3><#return "Approved"><#break>
        <#case 4><#return "Qualified"><#break>
        <#case 5><#return "Loved"><#break>
        <#default><#return "Unknown"><#break>
    </#switch>
</#function>

<#function convertModeBack mode>
    <#switch mode>
        <#case 0><#return "osu"><#break>
        <#case 4><#return "OSURX"><#break>
        <#case 8><#return "OSUAP"><#break>
        <#case 1><#return "taiko"><#break>
        <#case 2><#return "catch"><#break>
        <#case 3><#return "mania"><#break>
        <#case 5><#return "TAIKORX"><#break>
        <#case 6><#return "CATCHRX"><#break>
        <#default><#return "Unknown"><#break>
    </#switch>
</#function>

<#function convertModeIcon toIcon>
    <#switch mode>
        <#case 1><#return "/img/modes/taiko.svg"><#break>
        <#case 2><#return "/img/modes/catch.svg"><#break>
        <#case 3><#return "/img/modes/mania.svg"><#break>
        <#default><#return "/img/modes/osu.svg"><#break>
    </#switch>
</#function>

<#function convertPlaytime(totalMinutes)>
    <#assign days = totalMinutes / (24 * 60) />
    <#assign hours = (totalMinutes % (24 * 60)) / 60 />
    <#assign minutes = totalMinutes % 60 />
    
    <#return {
        "days": days?int,      
        "hours": hours?int,  
        "minutes": minutes?int 
    }>
</#function>

<#function convertModeToRelax(mode)>
    <#switch mode>
        <#case 0><#return 4><#break>
        <#case 1><#return 5><#break>
        <#case 2><#return 6><#break>
        <#case 8><#return 4><#break>
    </#switch>
</#function>