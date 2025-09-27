import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { currencies } from "./CurrencySelector";

// Zod validation schema
const InvoiceLineSchema = z.object({
  description: z.string().min(1, "Required"),
  amount: z.number().positive("Must be > 0"),
  currency: z.enum(currencies as [string, ...string[]]),
});

const InvoiceSchema = z.object({
  date: z.date().refine((d) => d >= new Date("1999-01-04"), {
    message: "Date must be after 1999-01-04",
  }),
  currency: z.enum(currencies as [string, ...string[]]),
  line: InvoiceLineSchema,
});

export type InvoiceFormData = z.infer<typeof InvoiceSchema>;

interface InvoiceFormWrapperProps {
  children: (props: {
    control: any;
    handleSubmit: any;
    errors: any;
    onSubmit: (data: InvoiceFormData) => void;
  }) => React.ReactNode;
  onSubmit?: (data: InvoiceFormData) => void;
  defaultValues?: Partial<InvoiceFormData>;
}

export default function InvoiceFormWrapper({
  children,
  onSubmit,
  defaultValues,
}: InvoiceFormWrapperProps) {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<InvoiceFormData>({
    resolver: zodResolver(InvoiceSchema),
    defaultValues: {
      date: new Date(),
      currency: "NZD",
      line: { description: "", amount: 0, currency: "USD" },
      ...defaultValues,
    },
  });

  const handleFormSubmit = (data: InvoiceFormData) => {
    if (onSubmit) {
      onSubmit(data);
    } else {
      console.log("Form submit:", data);
    }
  };

  return (
    <>
      {children({
        control,
        handleSubmit: handleSubmit(handleFormSubmit),
        errors,
        onSubmit: handleFormSubmit,
      })}
    </>
  );
}
