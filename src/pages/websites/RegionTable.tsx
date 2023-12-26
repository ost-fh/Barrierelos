import {DataGrid, GridColDef, GridPaginationModel, GridValueFormatterParams} from "@mui/x-data-grid";
import {useEffect, useState} from "react";
import useSWRMutation from "swr/mutation";
import {ResultRegion, WebsiteControllerService} from "../../lib/api-client";
import {formatScore} from "../../util/formatter.ts";
import LoadingIndicator from "../../components/LoadingIndicator.tsx";
import {useTranslation} from "react-i18next";

export function RegionTable() {
  const {t} = useTranslation();

  const [rows, setRows] = useState([] as TableRow[]);
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
  } as GridPaginationModel);

  const {
    data: regionPage,
    error,
    isMutating: isLoading,
    trigger
  } = useSWRMutation<ResultRegion, Error>("websites", getRegions)
  useEffect(() => {
    void trigger()
  }, [paginationModel])

  const [rowCount, setRowCount] = useState(
    regionPage?.totalElements || 20,
  );
  useEffect(() => {
    setRowCount((prevRowCount) =>
      regionPage?.totalElements !== undefined
        ? regionPage.totalElements
        : prevRowCount,
    );
  }, [regionPage?.totalElements, setRowCount]);

  if (error) return `Error occurred:\n${error}`
  if (isLoading) return (<LoadingIndicator/>)

  const columns: GridColDef[] = [
    {
      field: "region",
      headerName: t("WebsitesPage.RegionTable.regionHeaderLabel"),
      flex: 50,
      sortable: false,
      // @ts-expect-error: Type is automatically generated from the translation file and consists of many union types
      valueFormatter: (params: GridValueFormatterParams<Canton>) => t(`WebsiteTags.${params.value}`),
    },
    {
      field: "score",
      headerName: t("WebsitesPage.RegionTable.scoreHeaderLabel"),
      width: 140,
      sortable: false,
      align: "center",
    },
  ];

  return (
    <DataGrid
      rows={rows}
      columns={columns}
      rowCount={rowCount}
      loading={isLoading}
      paginationModel={paginationModel}
      pageSizeOptions={[10]}
      disableRowSelectionOnClick
      disableColumnMenu
      pagination
      paginationMode="server"
      onPaginationModelChange={setPaginationModel}
      aria-label={t("WebsitesPage.RegionTable.ariaLabel")}
    />
  )

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  async function getRegions(_: string) {
    const regionPage = await WebsiteControllerService.getRegions(
      paginationModel.page,
      paginationModel.pageSize,
    )
    const regions = regionPage.content
    setRows(regions?.map((region) => ({
      id: region.id,
      region: region.name,
      score: formatScore(region.score),
    }) as TableRow) || [])
    setRowCount(regionPage.totalElements)

    return regionPage
  }
}

interface TableRow {
  id: number
  region: string
  score: string
}
