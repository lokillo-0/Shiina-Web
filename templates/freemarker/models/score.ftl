<div class="col col-12 <#if !full??>col-md-6</#if> act-entry <#if (index >= 4)> d-none</#if> d-flex flex-column mb-4">
    <#if isActivity??>
    <div class="row p-2 align-items-center">
        <div class="col-auto">
            <img class="flag" src="${avatarServer}/${score.id?c}" alt="">
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
    </#if>

    <#assign name = score.mapFilename?replace(".osu", "")>
<#assign sanitizedName = name?replace('.osu', '')>

<div class="osu-score-card mb-3">
    <div class="osu-score-container">
        <!-- Beatmap Background -->
        <div class="osu-beatmap-bg" style="background-image: url('/api/v1/thumb?setId=${score.set_id?c}')"></div>
        
        <!-- Content Overlay -->
        <div class="osu-score-content">
            <!-- Grade Badge -->
            <#include "/freemarker/gradeconvert.ftl">
            
            <!-- Score Info -->
            <div class="osu-score-info">
                <div class="osu-beatmap-title">
                    ${sanitizedName}<#if score.mods?has_content> <span class="mod-pill badge bg-warning bg-opacity-50 text-warning border border-warning">${score.mods?join(", ")}</span></#if>
                </div>
                
                <div class="osu-score-stats">
                    <div class="osu-pp-display">
                        ${score.pp?string("0")}<span class="pp-unit">pp</span>
                    </div>
                    <div class="osu-acc-display">
                        ${score.acc?string("0.00")}%
                    </div>
                </div>
            </div>
            
            <!-- Action Buttons -->
            <div class="osu-action-buttons">
                <a href="/scores/${score.score_id?c}" 
                   class="osu-action-btn osu-view-btn" 
                   title="View Score"
                   onmouseover="this.style.background='rgba(52,152,219,0.3)'; this.style.borderColor='#3498db';"
                   onmouseout="this.style.background='rgba(255,255,255,0.1)'; this.style.borderColor='rgba(255,255,255,0.2)';">
                    <i class="fas fa-eye"></i>
                </a>
                <a download href="${apiUrlPub}/v1/get_replay?id=${score.score_id?c}" 
                   class="osu-action-btn osu-download-btn" 
                   title="Download Replay"
                   onmouseover="this.style.background='rgba(46,204,113,0.3)'; this.style.borderColor='#2ecc71';"
                   onmouseout="this.style.background='rgba(255,255,255,0.1)'; this.style.borderColor='rgba(255,255,255,0.2)';">
                    <i class="fas fa-download"></i>
                </a>
            </div>
        </div>
    </div>
</div>
</div>