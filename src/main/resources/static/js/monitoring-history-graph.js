function loadHistoryGraph(elementId, timeSpots, cpuS) {
    // element = elementId

    console.log("history log - " + elementId);
// set the dimensions and margins of the graph
    let margin = {top: 10, right: 30, bottom: 30, left: 60},
        width = 550 - margin.left - margin.right,
        height = 200 - margin.top - margin.bottom;

// Adds the svg canvas
    let svg = d3.select("#" + elementId)
        .append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
        .append("g")
            .attr("transform",
                  "translate(" + margin.left + "," + margin.top + ")");


    console.log("CPU " + cpuS)
    console.log("Time " + timeSpots)
    // Add X axis --> it is a date format
    let x = d3.scaleLinear()
        .domain([0, d3.max(cpuS)])
        .range([0, width]);
    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom().scale(x));

    // Max value observed:
    // const max = timeSpots.values)

    let parseDate = d3.timeParse("%Y-%m-%dT%h:%s");
    timeSpots.forEach(function(d) {
        d[0] = parseDate(d[0]);
    });
    // Add Y axis
    let y = d3.scaleTime()
        .domain(d3.extent(timeSpots))
        .range([height, 0]);
    svg.append("g")
        .call(d3.axisLeft().scale(y));

    // // Set the gradient
    svg.append("linearGradient")
        .attr("id", "line-gradient")
        .attr("gradientUnits", "userSpaceOnUse")
        .attr("x1", 0)
        .attr("y1", y(0))
        .attr("x2", 0)
        .attr("y2", y(d3.map(timeSpots)))
        .selectAll("stop")
        .data([
            {offset: "0%", color: "blue"},
            {offset: "100%", color: "red"}
        ])
        .enter().append("stop")
        .attr("offset", function (d) {
            return d.offset;
        })
        .attr("stop-color", function (d) {
            return d.color;
        });

    svg.append("path")
        .datum([cpuS, timeSpots])
        .attr("fill", "none")
        .attr("stroke", "url(#line-gradient)")
        .attr("stroke-width", 2)
        .attr("d", d3.line()
            .x((d) => x(cpuS))
            .y((d) => y(timeSpots))
        )

// ** Update data section (Called from the onclick)
     let inter = setInterval(function () {
         updateData(timeSpots, cpuS);
     }, 5000);


    // function updateData(timeSpots, cpuS) {
    //
    //     console.log(timeSpots)
    //     console.log(cpuS)
    //     // Add X axis --> it is a date format
    //     let x = d3.scaleTime()
    //         .domain(timeSpots.length)
    //         .range([0, width]);
    //
    //     // Max value observed:
    //     const max = d3.max(cpuS)
    //
    //     // Add Y axis
    //     let y = d3.scaleLinear()
    //         .domain([0, max])
    //         .range([height, 0]);
    //
    //
    //
    //     // Select the section we want to apply our changes to
    //     // Set the gradient
    //     d3.select("#" + element).transition()
    //         .append("linearGradient")
    //         .attr("id", "line-gradient")
    //         .attr("gradientUnits", "userSpaceOnUse")
    //         .attr("x1", 0)
    //         .attr("y1", y(0))
    //         .attr("x2", 0)
    //         .attr("y2", y(max))
    //         .selectAll("stop")
    //         .data([
    //             {offset: "0%", color: "blue"},
    //             {offset: "100%", color: "red"}
    //         ])
    //         .enter().append("stop")
    //         .attr("offset", function (d) {
    //             return d.offset;
    //         })
    //         .attr("stop-color", function (d) {
    //             return d.color;
    //         });
    //
    //     // svg.append("path")
    //     //     .datum(data)
    //     //     .attr("fill", "none")
    //     //     .attr("stroke", "url(#line-gradient)" )
    //     //     .attr("stroke-width", 2)
    //     //     .attr("d", d3.line()
    //     //         .x(function(d) { return x(d.date) })
    //     //         .y(function(d) { return y(d.value) })
    //     //     )
    //     // Make the changes
    //     svg.select(".line")   // change the line
    //         .duration(750)
    //         .attr("d", valueline(cpuS));
    //     svg.select(".x.axis") // change the x axis
    //         .duration(750)
    //         .call(xAxis);
    //     svg.select(".y.axis") // change the y axis
    //         .duration(750)
    //         .call(yAxis);
    //
    //     // });
    // }

//    svg.exit().remove()
}
