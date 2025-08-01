<#if score.grade != "F">
<div class="osu-grade">
    <img src="/img/ranking/ranking-${score.grade}.png" alt="Grade ${score.grade}">
</div>
<#else>
<div class="osu-grade grade-f">
    <i class="fas fa-times"></i>
</div>
</#if>