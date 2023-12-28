import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from "@mui/material";

export interface ConfirmDialogProps {
  title: string;
  children: React.ReactNode;
  no: string;
  yes: string;
  open: boolean;
  setOpen: (open: boolean) => void;
  onNo: () => void;
  onYes: () => void;
}

const ConfirmDialog = (props: ConfirmDialogProps) => {
  const { title, children, no, yes, open, setOpen, onNo = () => {}, onYes = () => {} } = props;
  return (
    <Dialog
      open={open}
      onClose={() => {
        setOpen(false);
        onNo();
      }}
      aria-labelledby="confirm-dialog"
    >
      <DialogTitle id="confirm-dialog">{title}</DialogTitle>
      <DialogContent>{children}</DialogContent>
      <DialogActions>
        <Button
          variant="contained"
          onClick={() => {
            setOpen(false);
            onNo();
          }}
          color="primary"
        >
          {no}
        </Button>
        <Button
          variant="contained"
          onClick={() => {
            setOpen(false);
            onYes();
          }}
          color="primary"
        >
          {yes}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ConfirmDialog;
