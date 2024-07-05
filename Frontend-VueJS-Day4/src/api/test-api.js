import httpApi from "@/utils/http-api";
import { api } from "@/config";

export default {
  getDataByAPI: (url, query) => {
    return httpApi.getDataViaApi({
      url,
      queryParams: query,
    });
  },
  postDataViaAPI: (url, body) => {
    return httpApi.postDataViaApi({
      url,
      body,
    });
  },
  getDataById: (id) => {
    const url = `${api.getById.replace(":id", id)}`;
    console.log(url);
    return httpApi.getDataViaApi({ url });
  },
};
