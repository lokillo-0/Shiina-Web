<div class="col col-12 col-md-6 act-entry <#if (index >= 4)> d-none</#if> d-flex flex-column mb-4">
    <div class="row p-2 align-items-center">
        <div class="col-auto">
            <img class="flag" src="https://a.osunolimits.dev/${score.id}" alt="">
        </div>
        <div class="col-auto d-flex align-items-center">
            <span class="h6">
                ${score.name}
            </span>
            <small class="ms-2" data-timestamp-format="date" data-timestamp="${score.playTime}">
                ${score.playTime}
            </small>
        </div>
    </div>
    <div class="bg-secondary p-3 leaderboard-panel d-flex flex-grow-1" style="border-radius: 5px;">
        <div class="d-block d-lg-flex flex-grow-1">
            <div class="col-12 col-lg-5 d-flex justify-content-center">
                <img style="object-fit: cover; height: 100%;" class="img-fluid rounded-2" src="https://assets.ppy.sh/beatmaps/${score.set_id?c}/covers/card.jpg" alt="">
            </div>
            <div class="col-12 d-flex mt-2 mt-lg-0 col-lg-7 mx-2 d-flex flex-column justify-content-start justify-content-sm-between">
            <#assign passedMods=score.mods>
            <#assign name = score.mapFilename?replace(".osu", "")>
        
            <span class="ms-2 text-wrap" data-bs-toggle="tooltip" data-bs-placement="top" title='${name}'>
                ${name}
                <#include "/freemarker/modconvert.ftl">
            </span>
            <p class="fs-5 ms-2">
                ${score.pp}pp
            </p>
        </div>
    </div>
</div>
</div>