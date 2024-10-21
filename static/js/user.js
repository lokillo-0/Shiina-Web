var isLoading = false;
var out;
var bootstrapTextColor = getBootstrapTextColor();
var bootstrapTextTransparent = getBootstrapTextTransparent();

function loadScorePanel(grade, mapId, score, pp, acc, maxCombo, playTime, name, setId, scoreId,mods) {
    var output = '';

    output += '<div class="col col-12 act-entry d-flex flex-column mb-1">';

    // Score container
    output += '<div class="score-container bg-secondary score-panel d-flex flex-grow-1 position-relative" style="border-radius: 5px;">';
    output += '<div class="d-block d-lg-flex flex-grow-1">';

    // Beatmap cover image
    output += '<div class="col-12 col-lg-3 d-flex justify-content-center">';
    output += '<img style="object-fit: cover; height: 100%;" class="img-fluid rounded-2 w-100" src="https://assets.ppy.sh/beatmaps/' + setId + '/covers/card.jpg" alt="">';
    output += '</div>';

    // Score details
    output += '<div class="col-12 d-flex p-2 mt-2 mt-lg-0 col-lg-7 mx-2 d-flex flex-column justify-content-start justify-content-sm-between">';
    output += '<span class="ms-2 text-wrap">' + name.replace('.osu', '');

    if (mods.length > 0) {
        output += ' <span class="fw-bold">+ ' + mods.join(",") + '</span>';
    }

    output += '</span>';
    output += '<span class="fs-5 ms-2">' + pp + 'pp <span class="fs-6">(' + acc + '%)</span> <img src="/img/ranking/ranking-' + grade + '.png" alt="Grade" class="img-fluid me-3" style="height: 30px;"></span>';
    output += '</div>';

    // Icons for view and replay
    output += '<div class="icon-container-score d-flex align-items-center">';
    output += '<a href="/scores/' + scoreId + '" class="icon-link-score me-3"><i data-bs-toggle="tooltip" data-bs-placement="top" title="View Score" class="fas fa-eye"></i></a>';
    output += '<a href="/v1/get_replay?id=' + scoreId + '" class="icon-link-score"><i data-bs-toggle="tooltip" data-bs-placement="top" title="Download Replay" class="fas fa-download"></i></a>';
    output += '</div>';

    output += '</div></div></div>';

    return output;
}

function getBootstrapTextColor() {
    const element = document.getElementById('text');
    const computedStyle = window.getComputedStyle(element);
    return computedStyle.color;
}

function getBootstrapTextTransparent() {
    let color = getBootstrapTextColor();
    color = color.slice(0, -1) + ', 0.2)'; // Remove last char and add transparency
    return color;
}

function loadMore() {
    let offset = document.getElementById('offsetFirstPlaces');
    offset.value = parseInt(offset.value) + 5;
    loadFirstPlaces(reqUrl, false);
}


function loadFirstPlaces(apiUrl, firstLoad = true) {
    let offset = document.getElementById('offsetFirstPlaces');

    if(firstLoad) {
        offset.value = 0;
    }
    
    apiUrl += offset.value;

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
           
            let container = document.getElementById('firstPlaces');
            let countElement = document.getElementById('firstPlacesCount');
            
            countElement.innerHTML = '(' + data.count + ')';
            if(!data.hasNextPage) {
                let btn = document.getElementById('firstPlacesButton');
                btn.classList.add('disabled');
            }

            if(firstLoad)
                container.innerHTML = '';
            data.firstPlaces.forEach(firstPlace => {
                // Create a new element for each first place
                let element = document.createElement('div');
                element.innerHTML = loadScorePanel(firstPlace.grade, firstPlace.map_id, firstPlace, firstPlace.pp, firstPlace.acc, firstPlace.max_combo, firstPlace.play_time, firstPlace.map_name, firstPlace.map_set_id, firstPlace.score_id,firstPlace.mods);
                container.appendChild(element);
            });
        })
        .catch(error => console.error('Error:', error));
}


function winEvent() {
    if (out) {
        out.resize();
    }
}
document.addEventListener("turbo:load", () => {
    // Reset loading state
    isLoading = false;

    let loadedNew = document.getElementById('firstLoad');
    if(loadedNew == null) return;

    if(loadedNew.value == 'true') {
        loadedNew.value = 'false';
         // Load score panel first
        loadFirstPlaces(reqUrl);
    }
   

    // Re-initialize the chart after loading data
    if (data.length > 0 && values.length > 0) {
        let ctx = document.getElementById('myChart');

        if (out) {
            out.destroy(); // Destroy previous instance if it exists
        }

        out = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data,
                datasets: [{
                    label: '# of Plays',
                    data: values,
                    borderColor: bsPrimary,
                    borderWidth: 2,
                    tension: 0.1,
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                aspectRatio: 5,
                scales: {
                    x: {
                        grid: {
                            color: bootstrapTextTransparent
                        },
                        ticks: {
                            color: bootstrapTextColor
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: bootstrapTextTransparent
                        },
                        ticks: {
                            color: bootstrapTextColor
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }});
    }
});


// Add event listeners
document.addEventListener("turbo:load", loadEventTurbo);
window.addEventListener('resize', winEvent);

// Function to clean up before navigating away (unload chart and events)
function unloadEventTurbo() {
    window.removeEventListener('resize', winEvent);
    document.removeEventListener("turbo:load", loadEventTurbo);
    document.removeEventListener("turbo:before-cache", unloadEventTurbo);
    
    if (out != null) {
        out.destroy();
        out = null; // Clear reference
    }
}

document.addEventListener("turbo:before-cache", unloadEventTurbo);

