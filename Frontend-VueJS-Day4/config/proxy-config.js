const onProxyResHandler = async function (proxyRes) {
  if (proxyRes && proxyRes.headers["set-cookie"]) {
    const cookies = proxyRes.headers["set-cookie"].map((cookie) =>
      cookie.replace(/; secure/gi, "")
    );
    proxyRes.headers["set-cookie"] = cookies;
  }
};

const onProxyReqHandler = async function (proxyReq) {
  proxyReq.setHeader("accept-encoding", "gzip;q=0,deflate,sdch");
};

const TARGET = "http://localhost:8881";

const commonProxy = {
  "/product/": {
    target: TARGET,
    changeOrigin: true,
  },
};

module.exports = {
  commonProxy,
};
