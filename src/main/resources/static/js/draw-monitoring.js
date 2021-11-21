let gauge1;
let gauge2;
let graph1

// let timeSlots = [];
// let cpuS = [];

window.onload = function () {

    // getCpuUsage();
    // getMemoryUsage();

    setInterval(function () {
        getCpuUsage();
        getMemoryUsage()
        getCpuHistory()
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
//                 console.log("key " + key);
//                 timeSlots[i] = key;
                // console.log("value " + val);
                // cpuS[i] = val;
                // i++;
                dict.push({time: parseTime(key), load: val});
            });
            // cpu = data;
           if (graph1 == null) {
                graph1 = loadHistoryGraph("historyGraph1", dict);
           } else {
               graph1.update(data);
           }
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
