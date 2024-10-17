let tooltipList = []; // Keep track of initialized tooltips

loadEventTurbo = document.addEventListener("turbo:load", function () {
    const nodesWithTimestamp = document.querySelectorAll('[data-timestamp]');
    const nodesArray = Array.from(nodesWithTimestamp);

    if (document.getElementById('video') != undefined) {
        const video = document.getElementById('video');
        loadVideoWithDelay(video);
    }

    nodesArray.forEach(node => {
        let format;
        if (node.getAttribute('data-timestamp-format') == 'date') { format = false; } else { format = true; }
        node.innerHTML = timeUntil(node.getAttribute('data-timestamp'), format);
    });

    // Initialize Bootstrap tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

submitStartTurbo = document.addEventListener('turbo:submit-start', function () {
    Turbo.navigator.delegate.adapter.showProgressBar();
});

beforeVisitTurbo = document.addEventListener('turbo:before-visit', () => {
    
});

beforeRenderTurbo = document.addEventListener('turbo:before-render', () => {
   
});

// Remove tooltips and event listeners when navigating back
unloadEventTurbo = document.addEventListener("turbo:before-cache", function () {
    Turbo.navigator.delegate.adapter.showProgressBar();

    // Properly dispose of all tooltips before caching
    tooltipList.forEach(tooltip => {
        tooltip.dispose();
    });
    tooltipList = [];

    // Clean up event listeners
    document.removeEventListener("turbo:load", loadEventTurbo);
    document.removeEventListener("turbo:before-cache", unloadEventTurbo);
    document.removeEventListener("turbo:submit-start", submitStartTurbo);
    document.removeEventListener("turbo:before-visit", beforeVisitTurbo);
    document.removeEventListener("turbo:before-render", beforeRenderTurbo);
});

function loadLazyLoadImage(img) {
    if (img.complete && img.naturalHeight !== 0) {
        img.classList.add('loaded');
        img.previousElementSibling.style.display = 'none';
    } else {
        setTimeout(() => {
            img.classList.add('loaded');
            img.previousElementSibling.style.display = 'none';
        }, 300);
    }
}

function loadVideoWithDelay(video) {
    const loader = document.getElementById('spinner');
    if (!video.classList.contains('loaded')) {
        video.style.dislay = 'none';
        loader.style.display = 'block';
        setTimeout(() => {
            video.classList.add('loaded');
            loader.style.display = 'none';
            video.style.display = 'block';
        }, 3000);
    }
}


function lazyLoadNoImage(img, icon) {
    if (img.complete && img.naturalHeight !== 0) {
        img.classList.add('loaded');
        img.previousElementSibling.style.display = 'none';
    } else {
        random = Math.floor(Math.random() * 1000);
        setTimeout(() => {
            img.src = icon;
            img.classList.add('loaded');
            img.previousElementSibling.style.display = 'none';
        }, random);
    }
}

function timeUntil(dateInput, unix) {
    let inputDate;
    if (unix) {
        inputDate = new Date(dateInput * 1000);
    } else {
        inputDate = new Date(dateInput.replace(' ', 'T') + 'Z');
    }

    const currentDate = new Date();
    const differenceInSeconds = Math.floor((inputDate - currentDate) / 1000);

    const isPast = differenceInSeconds < 0;
    const absDifferenceInSeconds = Math.abs(differenceInSeconds);

    const timeUnits = [
        { unit: 'year', seconds: 29030400 },
        { unit: 'month', seconds: 2419200 },
        { unit: 'week', seconds: 604800 },
        { unit: 'day', seconds: 86400 },
        { unit: 'hour', seconds: 3600 },
        { unit: 'minute', seconds: 60 },
        { unit: 'second', seconds: 1 }
    ];

    let result = '';

    for (const { unit, seconds } of timeUnits) {
        const value = Math.floor(absDifferenceInSeconds / seconds);
        if (value > 0) {
            result += `${value} ${unit}${value > 1 ? 's' : ''} `;
            break;
        }
    }

    return isPast ? result.trim() + ' ago' : result.trim() + ' from now';
}

function removeParam(param) {
    let url = new URL(window.location.href);
    url.searchParams.delete(param);
    Turbo.visit(url);
}


function selectParam(param, value) {
    let url = new URL(window.location.href);
    url.searchParams.set(param, value);
    if (param != 'page' || param == 'country') {
        url.searchParams.set('page', 1);
    }
    if (param == 'mode') {
        url.searchParams.delete('country');
    }
    Turbo.visit(url);
}