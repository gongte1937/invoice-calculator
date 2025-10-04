import { postInvoiceTotal } from "./api";
import { Currency } from "../../constants";

// Mock axios with simplified setup
jest.mock("axios", () => {
  const mockPost = jest.fn();
  return {
    create: jest.fn(() => ({ post: mockPost })),
    __mockPost: mockPost,
  };
});

import axios from "axios";
const mockPost = (axios as any).__mockPost;

describe("API Functions", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe("postInvoiceTotal", () => {
    const mockPayload = {
      date: new Date("2024-01-15"),
      currency: Currency.NZD,
      lines: [
        { description: "Test item", amount: 100, currency: Currency.USD },
      ],
    };

    it("successfully posts invoice data and returns total", async () => {
      const expectedTotal = "150.25 NZD";
      mockPost.mockResolvedValueOnce({ data: expectedTotal });

      const result = await postInvoiceTotal(mockPayload);

      expect(result).toBe(expectedTotal);
      expect(mockPost).toHaveBeenCalledWith(
        "/invoice/total",
        { invoice: mockPayload },
        { responseType: "text" }
      );
    });

    it("wraps payload in invoice object", async () => {
      mockPost.mockResolvedValueOnce({ data: "100.00 USD" });

      await postInvoiceTotal(mockPayload);

      expect(mockPost).toHaveBeenCalledWith(
        "/invoice/total",
        { invoice: mockPayload },
        { responseType: "text" }
      );
    });

    it("throws error when API returns error", async () => {
      const errorMessage = "Invalid currency conversion";
      mockPost.mockRejectedValueOnce({
        response: { data: errorMessage },
      });

      await expect(postInvoiceTotal(mockPayload)).rejects.toThrow(errorMessage);
    });

    it("throws generic error for network failures", async () => {
      mockPost.mockRejectedValueOnce(new Error("Network Error"));

      await expect(postInvoiceTotal(mockPayload)).rejects.toThrow(
        "Error: Unexpected response"
      );
    });
  });
});
