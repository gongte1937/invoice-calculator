import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { DEFAULT_BASE_CURRENCY, DEFAULT_LINE_CURRENCY } from "../../constants";
import { InvoiceSchema, type InvoiceFormData } from "@/lib/validation";

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
      currency: DEFAULT_BASE_CURRENCY,
      lines: [
        { description: "", amount: null, currency: DEFAULT_LINE_CURRENCY },
      ],
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
