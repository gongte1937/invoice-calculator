import { InvoiceFormData } from "@/lib/validation";
import axios from "axios";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE,
  headers: { "Content-Type": "application/json" },
});

export async function postInvoiceTotal(
  payload: InvoiceFormData
): Promise<string> {
  try {
    // Convert Date to yyyy-MM-dd format for backend LocalDate
    const formattedPayload = {
      ...payload,
      date: payload.date.toISOString().split("T")[0],
    };

    // Wrap the payload in the expected format
    const requestBody = { invoice: formattedPayload };
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
