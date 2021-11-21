function loadHistoryGraph(elementId, data) {

    let margin = {top: 10, right: 30, bottom: 30, left: 60},
        width = 550 - margin.left - margin.right,
        height = 200 - margin.top - margin.bottom;

    let svg = d3.select("#" + elementId)
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    console.log(data)

    let x = d3.scaleTime()
        .domain(d3.extent(data, function(d) { return d.time; }))
        .range([0, width]);
    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x)
            .tickFormat(d3.timeFormat("%H:%M:%S"))
            .ticks(5));

    // Add Y axis
    let y = d3.scaleLinear()
        .domain([0, d3.max(data, function(d) { return +d.load; })])
        .range([height, 0]);
    svg.append("g")
        .call(d3.axisLeft(y)
            .ticks(5));

    svg.append("path")
        .datum(data)
        .attr("fill", "none")
        .attr("stroke", "steelblue")
        .attr("stroke-width", 1.5)
        .attr("d", d3.line()
            .x(function(d) { return x(d.time) })
            .y(function(d) { return y(d.load) })
        )
}