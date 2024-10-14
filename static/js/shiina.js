loadEventTurbo = document.addEventListener("turbo:load", function() {
    const nodesWithTimestamp = document.querySelectorAll('[data-timestamp]');
    const nodesArray = Array.from(nodesWithTimestamp);

    nodesArray.forEach(node => {
        let fomat;
        if(node.getAttribute('data-timestamp-format') == 'date') { format = false; } else { format = true; }
        node.innerHTML = timeUntil(node.getAttribute('data-timestamp'),format);
    });
});

unloadEvenetTurbo = document.addEventListener("turbo:before-cache", function() {
    document.removeEventListener("turbo:load", loadEventTurbo);
    document.removeEventListener("turbo:before-cache", unloadEvenetTurbo);  
});

function timeUntil(dateInput, unix) {
    let inputDate;
    if(unix) {
        console.log('unix');
        inputDate = new Date(dateInput * 1000);
    }else {
        inputDate = new Date(dateInput.replace(' ', 'T') + 'Z');
    }
    
    const currentDate = new Date();
    console.log(inputDate)
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