import {WebsiteDetails} from "../../lib/api-client";
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import "./WebsiteOverview.css"


interface WebsiteOverviewProps {
  websiteDetails: WebsiteDetails
}

function WebsiteOverview(params: WebsiteOverviewProps) {
  const websiteDetails: WebsiteDetails = params.websiteDetails

  return (
    <>
      <h2>Webpages</h2>
      <TableContainer component={Paper}>
        <Table sx={{minWidth: 650}} aria-label="Webpages Table">
          <TableHead>
            <TableRow>
              <TableCell>URL</TableCell>
              {websiteDetails.statistics ? (
                <>
                  <TableCell>Barrierelos-Score</TableCell>
                  <TableCell>Weight</TableCell>
                </>
              ) : null}
            </TableRow>
          </TableHead>
          <TableBody>
            {websiteDetails.webpages.map((webpageDetails) => (
              <TableRow
                key={webpageDetails.id}
                sx={{'&:last-child td, &:last-child th': {border: 0}}}
              >
                <TableCell component="th" scope="row">{webpageDetails.webpage.url}</TableCell>
                {websiteDetails.statistics ? (
                  <>
                    <TableCell>{webpageDetails.statistics?.score}</TableCell>
                    <TableCell>10</TableCell>
                  </>
                ) : null}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </>
  )
}

export default WebsiteOverview