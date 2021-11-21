var margin = {top: 10, right: 30, bottom: 30, left: 60},
    width = 550 - margin.left - margin.right,
    height = 200 - margin.top - margin.bottom;

// Add X axis
var x = d3.scaleTime()
    .range([0, width]);

var xAxis = d3.axisBottom(x)
    .tickFormat(d3.timeFormat("%H:%M:%S"))
    .ticks(5);
// Add Y axis
var y = d3.scaleLinear()
    .range([height, 0]);

var yAxis = d3.axisLeft(y)
    .ticks(5);
var valueline = d3.line()
    .x(function(d) { return x(d.time); })
    .y(function(d) { return y(d.load); });

function loadHistoryGraph(elementId, data) {

    var svg = d3.select("#" + elementId)
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");
    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);
    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis);

    console.log(data)
    x.domain(d3.extent(data, function(d) { return d.time; }))
    y.domain([0, d3.max(data, function(d) { return +d.load; })])

    svg.append("path")
        .attr("class", "line")
        .attr("fill", "none")
        .attr("stroke", "steelblue")
        .attr("stroke-width", 1.5)
        .attr("d", valueline(data))
}

function updateData(elementId, data) {

    x.domain(d3.extent(data, function(d) { return d.time; }));
    y.domain([0, d3.max(data, function(d) { return d.load; })]);

    let svg = d3.select("#" + elementId).transition();

    // Make the changes
    svg.select(".line")   // change the line
        .duration(750)
        .attr("d", valueline(data));
    svg.select(".x.axis") // change the x axis
        .duration(750)
        .call(xAxis);
    svg.select(".y.axis") // change the y axis
        .duration(750)
        .call(yAxis);
}