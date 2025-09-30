import axios from "axios";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE,
  headers: { "Content-Type": "application/json" },
});

export async function postInvoiceTotal(payload: unknown): Promise<string> {
  try {
    // Wrap the payload in the expected format
    const requestBody = { invoice: payload };
    const res = await api.post<string>("/invoice/total", requestBody, {
      responseType: "text",
    });
    return res.data;
  } catch (err: any) {
    if (err.response?.data) {
      throw new Error(err.response.data);
    }
    throw new Error("Error: Unexpected response");
  }
}
