Cyan.importJs("../../highcharts/jquery.min.js");
Cyan.importJs("../../highcharts/highcharts.js");

Cyan.Chart.prototype.init = function (el)
{
    this.el = el;
};

Cyan.Chart.prototype.reload = function (callback)
{
    var chart = this;
    this.load(function (result)
    {
        chart.setData(result);

        if (callback)
            callback();
    });
};

Cyan.Chart.prototype.setData = function (data)
{
    this.data = data;
    if (this.highchart)
    {
        var chart = this;
        var record;
        var categories;
        for (var k = 0; k < data.length; k++)
        {
            record = this.data[k];
            if (record.name)
            {
                if (!categories)
                    categories = new Array(data.length);
                categories[k] = record.name;
            }
        }

        if (categories)
        {
            var xAxis = this.highchart.get("xAxis");
            if (xAxis)
                xAxis.setCategories(categories);
        }

        var pointevents = {
            click: function ()
            {
                if (chart.onpointclick)
                {
                    var key = this.x;
                    var name = null;
                    if (chart.data[key] && chart.data[key].name)
                    {
                        name = chart.data[key].name;
                        key = chart.data[key].key;
                    }

                    chart.onpointclick(this.series.name, key, name, this.y);
                }
            }
        };

        for (var i = 0; i < this.config.series.length; i++)
        {
            var serie = this.highchart.get("" + i);

            var r = 0, g = 0, b = 255;

            serie.setData([], true);

            for (var j = 0; j < this.data.length; j++)
            {
                record = this.data[j];
                var point = {};
                var type = serie.type || this.config.type;
                if (this.config.series.length == 1 && (type == "column" || type == "bar"))
                {
                    if (record.name)
                        point.x = j;
                    else
                        point.x = record.key;

                    point.key = record.key;
                    point.name = record.name;
                    point.y = record.values[i];
                    var s = (r * 255 * 255 + g * 255 + b).toString(16);
                    while (s.length < 6)
                        s = "0" + s;
                    point.color = "#" + s;
                    r += 10;
                    g += 5;
                    b -= 10;
                }
                else
                {
                    if (record.name)
                        point.x = j;
                    else
                        point.x = record.key;
                    point.key = record.key;
                    point.name = record.name;
                    point.y = record.values[i];
                }

                point.events = pointevents;

                serie.addPoint(point);
            }
        }
    }
};

Cyan.Chart.prototype.render = function ()
{
    var chart = this;
    Cyan.run(function ()
    {
        return window.Highcharts;
    }, function ()
    {
        chart.render0();
    });
};

Cyan.Chart.prototype.render0 = function ()
{
    if (!this.highchart)
    {
        var chart = this;
        var config = this.config;

        var title = Cyan.Chart.changeTitle(config.title);

        if (!title.style)
        {
            title.style = {
                fontWeight: "bold",
                color: "black"
            };
        }

        var legend = Cyan.Chart.changeLegend(config.legend);
        if (config.series.length == 1)
            legend.enabled = false;

        var xAxis = Cyan.Chart.changeAxis(config.xAxis);
        var yAxis = Cyan.Chart.changeAxis(config.yAxis);
        yAxis.type = "integer";

        if (config.xAxis.labelRotation < 0)
        {
            if (!xAxis.labels.style)
            {
                xAxis.labels.style = {
                    fontSize: "13px",
                    fontWeight: "bold",
                    color: "black"
                };
            }
            if (!xAxis.labels.align)
                xAxis.labels.align = "right";
        }
        xAxis.id = "xAxis";

        var chartOptions = {
            chart: {
                renderTo: this.el,
                defaultSeriesType: config.type == "ring" ? "pie" : config.type,
                inverted: config.inverted,
                events: {
                    click: function (e)
                    {
                        if (chart.onclick)
                        {
                            chart.onclick();
                        }
                    }
                }
            },
            title: title,
            subtitle: Cyan.Chart.changeTitle(config.subTitle),
            legend: legend,
            xAxis: xAxis,
            yAxis: yAxis,
            tooltip: {
                formatter: function ()
                {
                    var key = this.point.key;
                    var name = null;
                    for (var i = 0; i < chart.data.length; i++)
                    {
                        if (chart.data[i].key == key)
                        {
                            name = chart.data[i].name;
                            break;
                        }
                    }
                    var value = this.y;

                    if (chart.getToolTip)
                    {
                        return chart.getToolTip(this.series, key, name, value)
                    }
                    else
                    {
                        return name + ":" + value;
                    }
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        connectorColor: '#000000',
                        fontSize: "12px",
                        formatter: function ()
                        {
                            var key = this.point.key;
                            var name = null;
                            for (var i = 0; i < chart.data.length; i++)
                            {
                                if (chart.data[i].key == key)
                                {
                                    name = chart.data[i].name;
                                    break;
                                }
                            }
                            var value = this.y;

                            if (chart.getLabelToolTip)
                            {
                                return chart.getLabelToolTip(this.series, key, name, value)
                            }
                            else
                            {
                                return name + ":" + value;
                            }
                        }
                    }
                },
                column: {
                    stacking: config.stacking,
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        style: {
                            fontSize: "13px"
                        },
                        formatter: function ()
                        {
                            var key = this.point.key;
                            var name = null;
                            for (var i = 0; i < chart.data.length; i++)
                            {
                                if (chart.data[i].key == key)
                                {
                                    name = chart.data[i].name;
                                    break;
                                }
                            }
                            var value = this.y;

                            if (chart.getDataLabel)
                            {
                                return chart.getDataLabel(this.series, key, name, value)
                            }
                            else
                            {
                                return value;
                            }
                        }
                    }
                }
            },
            credits: {enabled: false}
        };


        chartOptions.series = [];

        var b = config.series.length > 1 && (this.config.type == "pie" || this.config.type == "ring"), size;
        if (b)
            size = parseInt(70 / (config.series.length + 1));

        for (var i = 0; i < config.series.length; i++)
        {
            var serie = {
                id: "" + i,
                name: config.series[i].name,
                data: []
            };
            if (config.series[i].color)
            {
                serie.color = config.series[i].color;
            }

            if (size)
            {
                serie.size = (size * (i + 2)) + "%";
                serie.innerSize = size * (i + 1) + "%";
            }
            else if (this.config.type == "ring")
            {
                serie.innerSize = "50%";
            }

            chartOptions.series.push(serie);
        }

        var highchart = this.highchart = new Highcharts.Chart(chartOptions);

        this.reload();

        var svg = Cyan.$$$(this.el).$("svg")[0];
        if (svg)
        {
            if (!Cyan.navigator.isIE() || Cyan.navigator.version < 10)
            {
                var nodes = svg.childNodes;
                for (var j = 0; j < nodes.length; j++)
                {
                    nodes[j].style.fill = "url()";
                }
            }
        }
        else
        {
            var shape = null;
            Cyan.$$$(this.el).eachElement(function ()
            {
                if (this.filled == true)
                {
                    shape = this;
                }
            });

            if (shape)
                shape.filled = false;
        }
    }
};

Cyan.Chart.prototype.setTitle = function (title)
{
    this.highchart.setTitle({text: title});
};

Cyan.Chart.prototype.setSubTitle = function (title)
{
    this.highchart.setTitle(null, {text: title});
};

Cyan.Chart.changeTitle = function (title)
{
    if (title && title.text)
    {
        var style = {fontWeight: "bold"};
        if (title.color)
            style.color = title.color;
        else
            style.color = "black";
        if (title.fontSize)
            style.fontSize = title.fontSize;
        return {
            text: title.text,
            style: style
        }
    }
    else
    {
        return {
            text: null
        };
    }
};

Cyan.Chart.changeLegend = function (legend)
{
    if (legend)
    {
        return {
            enabled: legend.enabled
        }
    }
    else
    {
        return {};
    }
};

Cyan.Chart.changeAxis = function (axis)
{
    if (axis)
    {
        var formatter = axis.labelFormatter;
        if (formatter)
        {
            var formatter0 = Cyan.toFunction(formatter, "value");
            formatter = function ()
            {
                return formatter0(this.value);
            };
        }

        var style = {
            fontWeight: "bold"
        };

        if (axis.color)
            style.color = axis.color;
        else
            style.color = "black";

        if (axis.fontSize)
            style.fontSize = axis.fontSize;
        else
            style.fontSize = "13px";

        var result = {
            title: Cyan.Chart.changeTitle(axis.title),
            categories: axis.categories,
            startOnTick: false,
            endOnTick: false,
            labels: {
                rotation: axis.labelRotation,
                formatter: formatter,
                style: style
            },
            showFirstLabel: true,
            showLastLabel: true,
            max: axis.max,
            min: axis.min,
            allowDecimals: axis.allowDecimals
        };

        if (axis.interval)
            result.tickInterval = axis.interval;

        return result;
    }
    else
    {
        return {
            showLastLabel: true
        };
    }
};

Cyan.Chart.prototype.reRender = function (config)
{
    var title, subTitle;
    if (config.title)
    {
        title = Cyan.Chart.changeTitle(config.title);

        if (!title.style)
        {
            title.style = {
                fontWeight: "bold",
                color: "black"
            };
        }
    }

    if (config.subTitle)
    {
        subTitle = Cyan.Chart.changeTitle(config.subTitle);
    }

    this.highchart.setTitle(title, subTitle);
};