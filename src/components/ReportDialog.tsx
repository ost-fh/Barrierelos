import {
  Box,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from "@mui/material";
import ConfirmDialog from "./ConfirmDialog.tsx";
import {useState} from "react";
import {useTranslation} from "react-i18next";
import {Report, User, Webpage, Website} from "../lib/api-client";
import reasonEnum = Report.reason;

export interface ReportDialogProps {
  subject: Website|Webpage|User;
  open: boolean;
  setOpen: (open: boolean) => void;
  onReportNo: () => void;
  onReportYes: (reason: reasonEnum, explanation: string) => void;
}

const ReportDialog = (props: ReportDialogProps) => {
  const { open, setOpen, onReportNo, onReportYes } = props;
  const {t} = useTranslation();
  const [reason, setReason] = useState(reasonEnum.INCORRECT);
  const [explanation, setExplanation] = useState("");
  const [explanationInvalid, setExplanationInvalid] = useState(false);

  const onReport = () => {
    if(explanation === "") {
      setOpen(true);
      setExplanationInvalid(true);
    }
    else {
      onReportYes(reason, explanation);
    }
  }

  return (
    <ConfirmDialog
      title={t('ReportComponent.title')}
      no={t('ReportComponent.buttonCancel')}
      yes={t('ReportComponent.buttonReport')}
      open={open}
      setOpen={setOpen}
      onNo={onReportNo}
      onYes={onReport}
    >
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'stretch' }}>
        <InputLabel id="label-reason">{t("ReportComponent.labelReason")}</InputLabel>
        <Select
          id="reason"
          labelId="label-reason"
          value={reason}
          onChange={(event) => setReason(reasonEnum[event.target.value as string as keyof typeof reasonEnum])}
        >
          <MenuItem value={reasonEnum.INCORRECT} selected={true}>{t('ReasonEnum.INCORRECT')}</MenuItem>
          <MenuItem value={reasonEnum.MISLEADING}>{t('ReasonEnum.MISLEADING')}</MenuItem>
          <MenuItem value={reasonEnum.INAPPROPRIATE}>{t('ReasonEnum.INAPPROPRIATE')}</MenuItem>
        </Select>
        <TextField
          margin="normal"
          required
          id="explanation"
          name="explanation"
          label={t("ReportComponent.labelExplanation")}
          sx={{ width: 'min(550px, 80vw)' }}
          multiline
          rows={4}
          error={explanationInvalid}
          value={explanation}
          onChange={(event) => {
            setExplanationInvalid(false);
            setExplanation(event.target.value);
          }}
        />
      </Box>
    </ConfirmDialog>
  );
}

export default ReportDialog;
