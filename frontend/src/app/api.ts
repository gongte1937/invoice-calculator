import { InvoiceFormData } from "@/components/form/InvoiceFormWrapper";
import axios from "axios";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE,
  headers: { "Content-Type": "application/json" },
});

export async function postInvoiceTotal(
  payload: InvoiceFormData
): Promise<string> {
  const mockPayloads = {
    invalidStructure: {
      date: new Date("2024-01-15"),
      currency: "USD",
      lines: [{ description: "Test item", amount: 100, currency: "USD" }],
    },
  };
  try {
    // Wrap the payload in the expected format
    const requestBody = { invoice: mockPayloads };
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
