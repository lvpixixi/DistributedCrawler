!function() {
    var p, q, r, a = function() {
        var b, c, d, e, a = document.getElementsByTagName("script");
        for (b = 0,
        c = a.length; c > b; b++)
            if (e = a[b],
            e.src && (d = /^(https?:)\/\/[\w\.\-]+\.cnzz\.com\//i.exec(e.src)))
                return d[1];
        return window.location.protocol
    }(), b = encodeURIComponent, c = "3302454", d = "", e = "", f = "online_v3.php", g = "hzs10.cnzz.com", h = "1", i = "text", j = "z", k = "&#31449;&#38271;&#32479;&#35745;", l = window["_CNZZDbridge_" + c]["bobject"], m = "0", n = a + "//online.cnzz.com/online/" + f, o = [];
    o.push("id=" + c),
    o.push("h=" + g),
    o.push("on=" + b(e)),
    o.push("s=" + b(d)),
    n += "?" + o.join("&"),
    "0" === m && l["callRequest"]([a + "//cnzz.mmstat.com/9.gif?abc=1"]),
    h && ("" !== e ? l["createScriptIcon"](n, "utf-8") : (q = "z" == j ? "https://www.cnzz.com/stat/website.php?web_id=" + c : "https://quanjing.cnzz.com",
    "pic" === i ? (r = a + "//icon.cnzz.com/img/" + d + ".gif",
    p = "<a href='" + q + "' target=_blank title='" + k + "'><img border=0 hspace=0 vspace=0 src='" + r + "'></a>") : p = "<a href='" + q + "' target=_blank title='" + k + "'>" + k + "</a>",
    l["createIcon"]([p])))
}();
(function() {
    var u = function() {
        for (var a = document.getElementsByTagName("script"), b = 0, d = a.length; b < d; b++) {
            var f, e = a[b];
            if (e.src && (f = /^(https?:)\/\/[\w\.\-]+\.cnzz\.com\//i.exec(e.src)))
                return f[1]
        }
        return window.location.protocol
    }();
    try {
        var m = "http://hm2.cnzz.com/"
          , q = ['http://www.bejson.com/']
          , g = document
          , f = window
          , n = encodeURIComponent
          , r = "unknow"
          , l = null
          , t = function() {
            this.c()
        };
        t.prototype = {
            c: function() {
                if (!1 === this.f())
                    return !1;
                this.a(g, "mousedown", this.b);
                var a = f.navigator.userAgent;
                l = g.documentElement && 0 !== g.documentElement.clientHeight ? g.documentElement : g.body;
                a = a ? a.toLowerCase().replace(/-/g, "") : "";
                for (var b = "netscape;se 1.;se 2.;saayaa;360se;tencent;qqbrowser;mqqbrowser;maxthon;myie;theworld;konqueror;firefox;chrome;safari;msie 5.0;msie 5.5;msie 6.0;msie 7.0;msie 8.0;msie 9.0;msie 10.0;Mozilla;opera".split(";"), d = 0; d < b.length; d += 1)
                    if (-1 !== a.indexOf(b[d])) {
                        r = b[d];
                        break
                    }
            },
            a: function(a, b, d) {
                a.addEventListener ? a.addEventListener(b, d, !1) : a.attachEvent ? a.attachEvent("on" + b, d) : a["on" + b] = d
            },
            b: function(a) {
                a || (a = f[a]);
                var b = a.target || a.srcElement;
                "IMG" === b.tagName && (b = b.parentNode);
                b = "A" === b.tagName ? 1 : 0;
                var d = a.which || a.button
                  , p = a.clientX;
                a = a.clientY;
                var e = f.pageYOffset || l.scrollTop;
                p += f.pageXOffset || l.scrollLeft;
                a += e;
                e = l.clientWidth || f.innerWidth;
                var k = f.location.href
                  , c = [];
                c.push("id=3302454");
                c.push("x=" + p);
                c.push("y=" + a);
                c.push("w=" + e);
                c.push("s=" + f.screen.width + "x" + f.screen.height);
                c.push("b=" + r);
                c.push("c=" + d);
                c.push("r=" + n(g.referrer));
                c.push("a=" + b);
                "" !== f.location.hash && c.push("p=" + n(k));
                c.push("random=" + n(Date()));
                b = c.join("&");
                var h = new Image;
                h.onload = h.onerror = h.onabort = function() {
                    h = h.onload = h.onerror = h.onabort = null
                }
                ;
                "https:" === u && (m = m.replace(/^http:/, "https:"));
                h.src = m + "heatmap.gif?" + b;
                return !0
            },
            f: function() {
                var a = f.location.href
                  , b = !1
                  , d = "([{\\^$|)?+.]}".split("");
                f.location.pathname || (a += "/");
                for (var g = 0; g < q.length; g++) {
                    var e = q[g];
                    if (-1 !== e.indexOf("*")) {
                        for (var k = 0; k < d.length; k++) {
                            var c = "/\\" + d[k] + "/g";
                            e = e.replace(eval(c), "\\" + d[k])
                        }
                        c = "/\\*/g";
                        e = e.replace(eval(c), "(.*)");
                        c = RegExp(e, "i");
                        if (c.test(a)) {
                            b = !0;
                            break
                        }
                    } else if (e === a) {
                        b = !0;
                        break
                    }
                }
                return b
            }
        };
        new t
    } catch (a) {}
}
)();