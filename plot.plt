# gnuplot -e "datafile='${data}'; outputname='${output}'" foo.plg
set title 'Grafo'
set xlabel 'x'
set ylabel 'y'

set grid xtics nomxtics noytics nomytics noztics nomztics nortics nomrtics
set grid layerdefault lt 0 linecolor 0 linewidth 0.500,  lt 0 linecolor 0 linewidth 0.500
set key fixed center top vertical Right noreverse enhanced autotitle nobox

set style increment default
set style arrow 1

set xtics border out scale 1,0.5 mirror norotate  autojustify
set xtics  norangelimit
set ytics border out scale 1,0.5 nomirror norotate  autojustify
set ytics  norangelimit

set xrange [44 : 56]
set yrange [1.95 : 3.1]

set terminal png enhanced
set terminal png size 1024, 1024
set output OUTFILE

plot DATAFILE using 1:2:($3-$1):($4-$2) with vectors arrowstyle 1,\
     DATAFILE using 1:2:($5) with labels offset char -1, char -1 right,\
     DATAFILE using 3:4:($6) with labels offset char -1, char -1 right,\