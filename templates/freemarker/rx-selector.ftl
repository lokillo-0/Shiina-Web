<div
    class="my-2 col-12 col-md-auto my-lg-0 col-auto d-flex align-items-center justify-content-center">
    <div class="btn-group flex-wrap" role="group" aria-label="Mixed example">
        <button type="button" onclick="selectParam('mode', '<#if (mode == 0 || mode == 1 || mode == 2 || mode == 8)>${convertModeToRelax(mode)}</#if>')"
            class="btn btn-<#if (mode == 4 || mode == 5 || mode == 6)>primary<#else>secondary</#if>" <#if (mode==3)>disabled</#if>>Relax</button>
        <button type="button" onclick="selectParam('mode', '<#if (mode == 8)>0<#else>8</#if>')"
            class="btn btn-<#if mode == 8>primary<#else>secondary</#if>" <#if (mode !=0 && mode !=4
            && mode !=8)>disabled</#if>>AutoPilot</button>
    </div>
</div>