let element

function loadHistoryGraph(elementId, data) {
    element = elementId

// set the dimensions and margins of the graph
    let margin = {top: 10, right: 30, bottom: 30, left: 60},
        width = 460 - margin.left - margin.right,
        height = 400 - margin.top - margin.bottom;

// Adds the svg canvas
    let svg = d3.select("#" + elementId)
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");


    // Add X axis --> it is a date format
    let x = d3.scaleLinear()
        .domain(data.key)
        .range([ 0, width ]);
    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

    // Max value observed:
    const max = d3.max(data)

    // Add Y axis
    var y = d3.scaleLinear()
        .domain([0, max])
        .range([ height, 0 ]);
    svg.append("g")
        .call(d3.axisLeft(y));

    // Set the gradient
    svg.append("linearGradient")
        .attr("id", "line-gradient")
        .attr("gradientUnits", "userSpaceOnUse")
        .attr("x1", 0)
        .attr("y1", y(0))
        .attr("x2", 0)
        .attr("y2", y(max))
        .selectAll("stop")
        .data([
            {offset: "0%", color: "blue"},
            {offset: "100%", color: "red"}
        ])
        .enter().append("stop")
        .attr("offset", function(d) { return d.offset; })
        .attr("stop-color", function(d) { return d.color; });

    svg.append("path")
        .datum(data)
        .attr("fill", "none")
        .attr("stroke", "url(#line-gradient)" )
        .attr("stroke-width", 2)
        .attr("d", d3.line()
            .x(function(d) { return x(d.date) })
            .y(function(d) { return y(d.value) })
        )
}

// ** Update data section (Called from the onclick)
let inter = setInterval(function () {
    updateData();
}, 5000);

function updateData(data) {

    // Add X axis --> it is a date format
    var x = d3.scaleTime()
        .domain(data.key)
        .range([ 0, width ]);

    // Max value observed:
    const max = d3.max(data)

    // Add Y axis
    var y = d3.scaleLinear()
        .domain([0, max])
        .range([ height, 0 ]);


    var svg = d3.select("#" + element).transition();

    // Select the section we want to apply our changes to
    // Set the gradient
    svg.append("linearGradient")
        .attr("id", "line-gradient")
        .attr("gradientUnits", "userSpaceOnUse")
        .attr("x1", 0)
        .attr("y1", y(0))
        .attr("x2", 0)
        .attr("y2", y(max))
        .selectAll("stop")
        .data([
            {offset: "0%", color: "blue"},
            {offset: "100%", color: "red"}
        ])
        .enter().append("stop")
        .attr("offset", function(d) { return d.offset; })
        .attr("stop-color", function(d) { return d.color; });

    // svg.append("path")
    //     .datum(data)
    //     .attr("fill", "none")
    //     .attr("stroke", "url(#line-gradient)" )
    //     .attr("stroke-width", 2)
    //     .attr("d", d3.line()
    //         .x(function(d) { return x(d.date) })
    //         .y(function(d) { return y(d.value) })
    //     )
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

    // });
}
