let gauge1;
let gauge2;
let graph1

window.onload = function () {

    // getCpuUsage();
    // getMemoryUsage();
    getCpuHistory();
    getMemoryHistory();

    setInterval(function () {
        getCpuUsage();
        getMemoryUsage();
        updateCpuHistory();
        updateMemoryHistory();
    }, 3000);
}

async function getCpuUsage() {
    $.ajax({
        type: 'Get',
        url: '/servers/get-cpu-usage',
        success: function (data) {
            console.log(data);
            // cpu = data;
            if (gauge1 == null) {
                gauge1 = loadLiquidFillGauge("fillgauge1", data);
            } else {
                gauge1.update(data);
            }
        }
    });
}

async function getCpuHistory() {
    $.ajax({
        type: 'Get',
        url: '/servers/get-cpu-history',
        success: function (data) {
            // console.log("Draw" + data);
            // let i = 0;
            let dict = [];
            let parseTime = d3.timeParse("%H:%M:%S");
            $.each(data, function(key, val){
// This function will called for each key-val pair.
// You can do anything here with them.
                dict.push({time: parseTime(key), load: val});
            });
                graph1 = loadHistoryGraph("historyGraph1", dict);
        }
    });
}

async function updateCpuHistory() {
    $.ajax({
        type: 'Get',
        url: '/servers/get-cpu-history',
        success: function (data) {
            let dict = [];
            let parseTime = d3.timeParse("%H:%M:%S");
            $.each(data, function(key, val){
                dict.push({time: parseTime(key), load: val});
            });
                graph1 = updateData("historyGraph1", dict);
        }
    });
}

async function getMemoryUsage() {
    $.ajax({
        type: 'Get',
        url: '/servers/get-memory-usage',
        success: function (data) {
            console.log(data);
            if (gauge2 == null) {
                gauge2 = loadLiquidFillGauge("fillgauge2", data);
            } else {
                gauge2.update(data);
            }
        }
    });
}

async function getMemoryHistory() {
    $.ajax({
        type: 'Get',
        url: '/servers/get-memory-history',
        success: function (data) {
            // console.log("Draw" + data);
            // let i = 0;
            let dict = [];
            let parseTime = d3.timeParse("%H:%M:%S");
            $.each(data, function(key, val){
// This function will called for each key-val pair.
// You can do anything here with them.
                dict.push({time: parseTime(key), load: val});
            });
            graph1 = loadHistoryGraph("historyGraph2", dict);
        }
    });
}

async function updateMemoryHistory() {
    $.ajax({
        type: 'Get',
        url: '/servers/get-memory-history',
        success: function (data) {
            let dict = [];
            let parseTime = d3.timeParse("%H:%M:%S");
            $.each(data, function(key, val){
                dict.push({time: parseTime(key), load: val});
            });
            graph1 = updateData("historyGraph2", dict);
        }
    });
}